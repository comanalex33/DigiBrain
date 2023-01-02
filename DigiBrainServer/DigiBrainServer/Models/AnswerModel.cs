using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class AnswerModel
    {
        public long Id { get; set; }
        public string Text { get; set; }
        public int Position { get; set; }
        public bool Correct { get; set; }
        public long QuestionId { get; set; }
        public bool RequestToResolve { get; set; }

        public AnswerModel() { }
        public AnswerModel(long Id, string Text, int Position, bool Correct, long QuestionId)
        {
            this.Id = Id;
            this.Text = Text;
            this.Position = Position;
            this.Correct = Correct;
            this.QuestionId = QuestionId;
            RequestToResolve = false;
        }

        public static explicit operator AnswerResponseModel(AnswerModel model)
        {
            return JsonConvert.DeserializeObject<AnswerResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
