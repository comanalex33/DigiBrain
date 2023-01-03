
namespace DigiBrainServer.ResponseModels
{
    public class AnswerResponseModel
    {
        public long Id { get; set; }
        public string Text { get; set; }
        public int Position { get; set; }
        public bool Correct { get; set; }
        public long QuestionId { get; set; }
    }
}
