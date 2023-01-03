

using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class QuizQuestionViewModel
    {
        [Required(ErrorMessage = "Quiz id required")]
        public long Id { get; set; }

        [Required(ErrorMessage = "Question id required")]
        public long QuestionId { get; set; }

        [Required(ErrorMessage = "Answer id required")]
        public long AnswerId { get; set; }
    }
}
