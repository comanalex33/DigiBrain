
namespace DigiBrainServer.ResponseModels
{
    public class QuestionResponseModel
    {
        public long Id { get; set; }
        public string Type { get; set; }
        public string Difficulty { get; set; }
        public string Text { get; set; }
        public long LanguageId { get; set; }
    }
}
