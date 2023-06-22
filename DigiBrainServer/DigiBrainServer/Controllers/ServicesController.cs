using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using FirebaseAdmin.Messaging;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/services")]
    [ApiController]
    public class ServicesController : ControllerBase
    {

        private readonly UserManager<UserModel> _userModel;

        public ServicesController(UserManager<UserModel> userModel)
        {
            _userModel = userModel;
        }

        [HttpGet]
        [Route("verify-email")]
        public async Task<ActionResult<InfoResponseModel>> VerifyEmail(string code)
        {
            var user = await _userModel.Users.FirstOrDefaultAsync(item => item.RegistrationToken == code);

            if (user == null)
            {
                return Ok(new { message = "Verification failed" });
            }

            if (user.EmailConfirmed == true)
            {
                return BadRequest(new { message = "Email already confirmed" });
            }

            user.EmailConfirmed = true;

            await _userModel.UpdateAsync(user);

            return Ok(new { message = "Verification successful, you can now login" });
        }

        [HttpPost]
        [Route("notify/title/{title}/message/{message}/topic/{topic}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<InfoResponseModel>> SendNotification(string title, string message, string topic)
        {
            var notification = new Message()
            {
                Data = new Dictionary<string, string>()
                {
                    ["FirstName"] = "Digi",
                    ["LastName"] = "Brain"
                },
                Notification = new Notification
                {
                    Title = title,
                    Body = message
                },
                Topic = topic
            };

            var messagingInstance = FirebaseMessaging.DefaultInstance;
            var result = await messagingInstance.SendAsync(notification);

            return Ok(new { message = result });
        }
    }
}
