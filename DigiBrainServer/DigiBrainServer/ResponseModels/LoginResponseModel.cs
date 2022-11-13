using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.ResponseModels
{
    public class LoginResponseModel
    {
        public string Token { get; set; }
        public DateTime Expiration { get; set; }
    }
}
