using System;

namespace DigiBrainServer.ResponseModels
{
    public class LoginResponseModel
    {
        public string Token { get; set; }
        public DateTime Expiration { get; set; }
    }
}
