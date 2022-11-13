using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Models
{
    public class SecretsModel
    {
        public string DatabaseHost { get; set; }
        public string DatabaseName { get; set; }
        public string DatabasePassword { get; set; }
        public string JWTSecret { get; set; }
    }
}
