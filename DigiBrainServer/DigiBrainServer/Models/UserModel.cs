﻿using DigiBrainServer.ResponseModels;
using Microsoft.AspNetCore.Identity;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class UserModel: IdentityUser
    {
        public long ClassId { get; set; }
        public bool RequestToResolve { get; set; }

        public static explicit operator UserResponseModel(UserModel user)
        {
            return JsonConvert.DeserializeObject<UserResponseModel>(JsonConvert.SerializeObject(user));
        }
    }
}
