using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class LanguageModel
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Code { get; set; }
        public bool RequestToResolve { get; set; }

        public LanguageModel() { }

        public LanguageModel(long Id, string Name, string Code)
        {
            this.Id = Id;
            this.Name = Name;
            this.Code = Code;
            RequestToResolve = false;
        }

        public static explicit operator LanguageResponseModel(LanguageModel language)
        {
            return JsonConvert.DeserializeObject<LanguageResponseModel>(JsonConvert.SerializeObject(language));
        }
    }
}
