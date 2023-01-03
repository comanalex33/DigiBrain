using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class AnswerViewModel
    {
        [Required(ErrorMessage = "Answer text required")]
        public string Text { get; set; }

        public int Position { get; set; }

        [Required(ErrorMessage = "Correct required")]
        public bool Correct { get; set; }

        [Required(ErrorMessage = "Question required")]
        public long QuestionId { get; set; }
    }
}
