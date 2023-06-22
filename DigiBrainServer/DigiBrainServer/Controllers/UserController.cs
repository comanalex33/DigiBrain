using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;
using DigiBrainServer.Services;

namespace DigiBrainServer.Controllers
{
    [Route("api/users")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly UserManager<UserModel> _userManager;
        private readonly SecretsModel _secretsManager;
        private readonly AppDbContext _context;
        public UserController(UserManager<UserModel> userManager, AppDbContext context, SecretsModel secretsManager)
        {
            _userManager = userManager;
            _secretsManager = secretsManager;
            _context = context;
        }

        [HttpGet]
        [Route("{username}")]
        public async Task<ActionResult<UserResponseModel>> GetUserByUsername(string username)
        {
            var user = await _userManager.FindByNameAsync(username);
            if(user == null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }

            var responseUser = (UserResponseModel)user;
            return Ok(responseUser);
        }

        [HttpGet]
        [Route("{username}/role")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<string>> GetUserRoleByUsername(string username)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }
            var roles = await _userManager.GetRolesAsync(user);
            if(roles.Count == 0)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This user does not have any roles assigned"
                });
            }

            return Ok(roles[0]);
        }

        [HttpGet]
        [Route("role/{roleName}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<IEnumerable<UserResponseModel>>> GetUsersByRole(string roleName)
        {
            IList<UserModel> users;

            if (roleName.Equals("all"))
            {
                users = await _userManager.Users.ToListAsync();
            }
            else if (roleName.Equals("none"))
            {
                var usersInAnyRole = await GetUsersInAnyRole();
                var allUsers = await _userManager.Users.ToListAsync();
                users = allUsers.Where(
                    user => !usersInAnyRole.Any(userInRole => userInRole.Id.Equals(user.Id))
                ).ToList();
            } 
            else
            {
                users = await _userManager.GetUsersInRoleAsync(roleName);
            }

            var responseUsers = new List<UserResponseModel>();
            foreach(var user in users)
            {
                responseUsers.Add((UserResponseModel)user);
            }

            return Ok(responseUsers);
        }

        [HttpGet]
        [Route("requests")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<IEnumerable<UserResponseModel>>> GetUsersWithRequests()
        {
            var usersWithRequests = await _userManager.Users.Where(item => item.RoleRequest != null).ToListAsync();

            var responseUsers = new List<UserResponseModel>();
            foreach (var user in usersWithRequests)
            {
                responseUsers.Add((UserResponseModel)user);
            }

            return Ok(responseUsers);
        }

        [HttpPut]
        [Route("{username}/roles/{role}")]
        [Authorize(Roles = "admin,student,teacher")]
        public async Task<ActionResult<UserResponseModel>> RequestRole(string username, string role)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "User does not exists"
                });
            }

            if (!role.Equals("student") && !role.Equals("teacher") && !role.Equals("admin"))
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "Not an existing role"
                });
            }

            user.RoleRequest = role;
            await _userManager.UpdateAsync(user);
            return Ok((UserResponseModel)user);
        }

        [HttpPut]
        [Route("{username}/roles/accept/{accept}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<UserResponseModel>> ChangeRole(string username, bool accept)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel 
                {
                   Message = "User does not exists"
                });
            }

            if(user.RoleRequest == null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "User did not requested a role change"
                });
            }

            if (accept)
            {
                // Delete previous role
                var roles = await _userManager.GetRolesAsync(user);
                if(roles.Count != 0)
                    await _userManager.RemoveFromRoleAsync(user, roles[0]);

                // Add new role
                await _userManager.AddToRoleAsync(user, user.RoleRequest);
            }

            user.RoleRequest = null;
            await _userManager.UpdateAsync(user);

            return Ok((UserResponseModel)user);
        }

        [HttpPut]
        [Route("{username}/classes/{classId}")]
        [Authorize(Roles = "admin,student")]
        public async Task<ActionResult<UserResponseModel>> ChangeClass(string username, long classId)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "User does not exists"
                });
            }

            var currentUser = await _userManager.FindByNameAsync(User.Identity.Name);
            if(currentUser.Id != user.Id)
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Not allowed to modify this user's data"
                });
            }

            var classModel = await _context.Class.FindAsync(classId);
            if(classModel == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Class does not exists"
                });
            }

            user.ClassId = classId;
            await _userManager.UpdateAsync(user);
            return Ok((UserResponseModel)user);
        }

        [HttpPut("{username}/profile-image")]
        public async Task<ActionResult<UserResponseModel>> UpdateProfileImage(string username, IFormFile file)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }

            var s3Handler = new S3Service(_secretsManager);
            var fileName = await s3Handler.UploadToS3(file);
            if (fileName == null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "File already exists"
                });
            }
            else
            {
                user.ProfileImageName = fileName;
                await _userManager.UpdateAsync(user);
                return Ok((UserResponseModel)user);
            }
        }

        [HttpDelete]
        [Route("{username}")]
        [Authorize(Roles = "admin,student,teacher")]
        public async Task<ActionResult<UserResponseModel>> DeleteUser(string username)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "User does not exists"
                });
            }

            var currentUser = await _userManager.FindByNameAsync(User.Identity.Name);
            if (currentUser.Id != user.Id)
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Not allowed to modify this user's data"
                });
            }

            var result = await _userManager.DeleteAsync(user);
            if (result == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "User couldn't be found"
                });
            }
            return Ok((UserResponseModel)user);
        }

        private async Task<List<UserModel>> GetUsersInAnyRole()
        {
            var admins = await _userManager.GetUsersInRoleAsync("admin");
            var students = await _userManager.GetUsersInRoleAsync("student");
            var teachers = await _userManager.GetUsersInRoleAsync("teacher");
            foreach (var student in students)
            {
                admins.Add(student);
            }
            foreach (var teacher in teachers)
            {
                admins.Add(teacher);
            }

            return admins.ToList();
        }
    }
}
