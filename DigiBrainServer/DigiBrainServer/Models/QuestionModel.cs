using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class QuestionModel
    {
        public long Id { get; set; }
        public string Type { get; set; }
        public string Difficulty { get; set; }
        public string Text { get; set; }
        public long LanguageId { get; set; }
        public bool RequestToResolve { get; set; }

        public QuestionModel() { }
        public QuestionModel(long Id, string Type, string Difficulty, string Text, long LanguageId)
        {
            this.Id = Id;
            this.Type = Type;
            this.Difficulty = Difficulty;
            this.Text = Text;
            this.LanguageId = LanguageId;
            RequestToResolve = false;
        }

        public static explicit operator QuestionResponseModel(QuestionModel model)
        {
            return JsonConvert.DeserializeObject<QuestionResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
