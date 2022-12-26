using System.Collections.Generic;

namespace DigiBrainServer.ResponseModels
{
    public class ErrorResponseModel
    {
        public string Message { get; set; }
        public List<string> InvalidFeilds { get; set; }
    }
}
