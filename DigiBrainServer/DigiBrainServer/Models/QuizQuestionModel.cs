
namespace DigiBrainServer.Models
{
    public class QuizQuestionModel
    {
        public long Id { get; set; }
        public long QuizId { get; set; }
        public long QuestionId { get; set; }
        public long SelectedAnswerId { get; set; }

        public QuizQuestionModel() { }
        public QuizQuestionModel(long Id, long QuizId, long QuestionId, long SelectedAnswerId)
        {
            this.Id = Id;
            this.QuizId = QuizId;
            this.QuestionId = QuestionId;
            this.SelectedAnswerId = SelectedAnswerId;
        }
    }
}
