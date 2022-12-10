using DigiBrainServer.ResponseModels;
using Microsoft.AspNetCore.Identity;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class UserModel: IdentityUser
    {
        public long classId { get; set; }
        public bool requestToResolve { get; set; }

        public static explicit operator UserResponseModel(UserModel user)
        {
            return JsonConvert.DeserializeObject<UserResponseModel>(JsonConvert.SerializeObject(user));
        }
    }
}
