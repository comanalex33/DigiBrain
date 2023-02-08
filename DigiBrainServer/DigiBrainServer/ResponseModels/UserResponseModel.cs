using Microsoft.AspNetCore.Identity;

namespace DigiBrainServer.ResponseModels
{
    public class UserResponseModel: IdentityUser
    {
        public long ClassId { get; set; }
        public string ProfileImageName { get; set; }
    }
}
