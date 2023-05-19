using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLessonQuizViewModel
    {
        [Required(ErrorMessage = "Learn path lesson quiz score required")]
        public long Score { get; set; }
        
        [Required(ErrorMessage = "Learn path lesson quiz question required")]
        public long QuestionId { get; set; }

        [Required(ErrorMessage = "Learn path lesson quiz parent required")]
        public long PathLessonId { get; set; }
    }
}
