using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLessonQuizViewModel
    {
        [Required(ErrorMessage = "Learn path lesson quiz number required")]
        public long Number { get; set; }

        [Required(ErrorMessage = "Learn path lesson quiz question required")]
        public long QuestionId { get; set; }

        [Required(ErrorMessage = "Learn path lesson quiz parent required")]
        public long PathLessonId { get; set; }
    }
}
