using Microsoft.AspNetCore.Identity;

namespace DigiBrainServer.ResponseModels
{
    public class UserResponseModel: IdentityUser
    {
        public long classId { get; set; }
    }
}
