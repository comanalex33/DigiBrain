using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.Services;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly IConfiguration _configuration;
        private readonly UserManager<UserModel> _userModel;
        private readonly RoleManager<RoleModel> _roleManager;
        private readonly SecretsModel _secretsManager;
        private readonly IEmailService _emailService;

        public AuthController(IConfiguration configuration, UserManager<UserModel> userModel, RoleManager<RoleModel> roleManager, SecretsModel secretsManager, IEmailService emailService)
        {
            _configuration = configuration;
            _userModel = userModel;
            _roleManager = roleManager;
            _secretsManager = secretsManager;
            _emailService = emailService;
        }

        [HttpGet]
        [Route("object-storage-info")]
        [Authorize(Roles = "admin,student,teacher")]
        public ActionResult<ObjectStorageModel> GetObjectStorageInfo()
        {
            return Ok(new ObjectStorageModel(
                _secretsManager.S3ReadAccessKey,
                _secretsManager.S3ReadSecretKey,
                _secretsManager.S3BucketName,
                _secretsManager.S3BucketRegion));
        }

        [HttpPost]
        [Route("login")]
        public async Task<ActionResult<LoginResponseModel>> Login(LoginViewModel model)
        {
            var user = await _userModel.FindByNameAsync(model.Username);

            if(user == null || !await _userModel.CheckPasswordAsync(user, model.Password))
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Invalid username or password"
                });
            }

            if(user.EmailConfirmed == false)
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Account not confirmed"
                });
            }

            var userRoles = await _userModel.GetRolesAsync(user);

            var authClaims = new List<Claim>
            {
                new ("name", user.UserName),
                new(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
                new(ClaimTypes.Name, model.Username)
            };

            authClaims.AddRange(userRoles.Select(userRole => new Claim("roles", userRole)));

            var authSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(string.Format(
                _configuration["JWT:Secret"],
                _secretsManager.JWTSecret)));

            var token = new JwtSecurityToken(
                _configuration["JWT:ValidIssuer"],
                _configuration["JWT:ValidAudience"],
                expires: DateTime.Now.AddHours(5),
                claims: authClaims,
                signingCredentials: new SigningCredentials(authSigningKey, SecurityAlgorithms.HmacSha256)
            );

            return Ok(new LoginResponseModel
            {
                Token = new JwtSecurityTokenHandler().WriteToken(token),
                Expiration = token.ValidTo
            });
        }

        [HttpPost]
        [Route("register")]
        public async Task<ActionResult<UserViewModel>> Register(RegisterViewModel model)
        {
            var rolesDoesNotExist = !await _roleManager.RoleExistsAsync("admin");
            var admins = await _userModel.GetUsersInRoleAsync("admin");

            if (rolesDoesNotExist || admins.Count == 0)
            {
                await AddRoles("admin");
                await AddRoles("student");
                await AddRoles("teacher");

                var admin = await AddUser(model, true);
                var adminUser = await _userModel.FindByNameAsync(model.Username);
                SendConfirmationEmail(adminUser, Request.Headers["origin"]);

                return admin;
            }

            var userWithSameName = await _userModel.FindByNameAsync(model.Username);
            if (userWithSameName != null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "User already exists, please login",
                    InvalidFeilds = new List<string> { nameof(model.Username) }
                });
            }

            var userWithSameEmail = await _userModel.FindByEmailAsync(model.Email);
            if (userWithSameEmail != null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "An account with this Email already exists",
                    InvalidFeilds = new List<string> { nameof(model.Email) }
                });
            }

            if (model.Password.Length < 8)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "Password must be at least 8 characters long",
                    InvalidFeilds = new List<string> { nameof(model.Password) }
                });
            }

            var user = await AddUser(model, false);
            var userModel = await _userModel.FindByNameAsync(model.Username);
            await _userModel.AddToRoleAsync(userModel, "student");
            SendConfirmationEmail(userModel, Request.Headers["origin"]);

            return user;
        }

        private async Task AddRoles(string role)
        {
            var roleValue = new RoleModel { Id = Guid.NewGuid().ToString(), Name = role };
            var result = await _roleManager.CreateAsync(roleValue);
            if (!result.Succeeded)
            {
                Console.WriteLine(result.Errors);
            }
        }

        private async Task<ActionResult<UserViewModel>> AddUser(RegisterViewModel model, bool makeAdmin)
        {
            var puser = new UserModel { UserName = model.Username, Email = model.Email, RegistrationToken = Guid.NewGuid().ToString() };
            var result = await _userModel.CreateAsync(puser, model.Password);
            if (!result.Succeeded)
            {
                return new UserViewModel { Errors = result.Errors.Select(err => err.Description) };
            }

            var user = await _userModel.FindByNameAsync(puser.UserName);
            if (makeAdmin)
            {
                await _userModel.AddToRoleAsync(user, "admin");
            }

            return Created("", new UserViewModel { Id = user.Id, Email = user.Email, UserName = user.UserName });
        }

        private void SendConfirmationEmail(UserModel user, string origin)
        {
            var verifyUrl = $"{origin}/api/services/verify-email?code={user.RegistrationToken}";
            var message = $@"<p>Please click the below link to verify your email address:</p>
                             <p><a href=""{verifyUrl}"">{verifyUrl}</a></p>";

            _emailService.Send(
                to: user.Email,
                subject: "Sign-up Verification API - Verify Email",
                html: $@"<h4>Verify Email</h4>
                         <p>Thanks for registering!</p>
                         {message}"
            );
        }
    }
}
