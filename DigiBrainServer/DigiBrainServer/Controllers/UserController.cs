using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace DigiBrainServer.Controllers
{
    [Route("api/users")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly UserManager<UserModel> _userManager;
        
        public UserController(UserManager<UserModel> userManager)
        {
            _userManager = userManager;
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
                    message = "This used does not exist"
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
                    message = "This used does not exist"
                });
            }

            var roles = await _userManager.GetRolesAsync(user);
            if(roles.Count == 0)
            {
                return NotFound(new ErrorResponseModel
                {
                    message = "This user does not have any roles assigned"
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
                var usersInAnyRole = await getUsersInAnyRole();
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

        [HttpPost]
        [Route("{username}/roles/{role}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult> AddRole(string username, string role)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel 
                {
                   message = "User does not exists"
                });
            }
            var roles = await _userManager.GetRolesAsync(user);
            if (roles.Any())
            {
                return BadRequest(new ErrorResponseModel
                {
                    message = "This user already has a role"
                });
            }
            if (!role.Equals("student") && !role.Equals("teacher"))
            {
                return BadRequest(new ErrorResponseModel
                {
                    message = "Not an existing role"
                });
            }
            await _userManager.AddToRoleAsync(user, role);
            return Ok();
        }

        [HttpDelete]
        [Route("{username}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<string>> DeleteUser(string username)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    message = "User does not exists"
                });
            }
            var result = await _userManager.DeleteAsync(user);
            if (result == null)
            {
                return NotFound();
            }
            return Ok();
        }

        private async Task<List<UserModel>> getUsersInAnyRole()
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
