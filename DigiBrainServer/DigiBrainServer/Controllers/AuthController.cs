using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
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

        public AuthController(IConfiguration configuration, UserManager<UserModel> userModel, RoleManager<RoleModel> roleManager, SecretsModel secretsManager)
        {
            _configuration = configuration;
            _userModel = userModel;
            _roleManager = roleManager;
            _secretsManager = secretsManager;
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
                    message = "Invalid username or password"
                });
            }

            var userRoles = await _userModel.GetRolesAsync(user);

            var authClaims = new List<Claim>
            {
                new ("name", user.UserName),
                new(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
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

            if (rolesDoesNotExist || admins.Count() == 0)
            {
                await AddRoles("admin");
                await AddRoles("student");
                await AddRoles("teacher");

                var admin = await AddUser(model, true);

                return admin;
            }

            var userWithSameName = await _userModel.FindByNameAsync(model.Username);
            if (userWithSameName != null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    message = "User already exists, please login",
                    invalidFeilds = new List<string> { nameof(model.Username) }
                });
            }

            var userWithSameEmail = await _userModel.FindByEmailAsync(model.Email);
            if (userWithSameEmail != null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    message = "An account with this Email already exists",
                    invalidFeilds = new List<string> { nameof(model.Email) }
                });
            }

            if (model.Password.Length < 8)
            {
                return BadRequest(new ErrorResponseModel
                {
                    message = "Password must be at least 8 characters long",
                    invalidFeilds = new List<string> { nameof(model.Password) }
                });
            }

            var user = await AddUser(model, false);

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
            var puser = new UserModel { UserName = model.Username, Email = model.Email };
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
    }
}
