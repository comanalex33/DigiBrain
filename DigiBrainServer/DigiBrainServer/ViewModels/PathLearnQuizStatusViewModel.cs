using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLearnQuizStatusViewModel
    {
        [Required(ErrorMessage = "Learn path quiz section number required")]
        public long SectionNumber { get; set; }

        [Required(ErrorMessage = "Learn path quiz lesson number required")]
        public long LessonNumber { get; set; }

        [Required(ErrorMessage = "Learn path quiz score required")]
        public long Score { get; set; }
    }
}
