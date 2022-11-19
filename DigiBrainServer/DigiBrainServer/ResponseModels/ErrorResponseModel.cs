using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.ResponseModels
{
    public class ErrorResponseModel
    {
        public string message { get; set; }
        public List<string> invalidFeilds { get; set; }
    }
}
