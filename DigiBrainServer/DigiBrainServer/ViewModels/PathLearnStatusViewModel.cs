using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLearnStatusViewModel
    {
        [Required(ErrorMessage = "Learn path section number required")]
        public long SectionNumber { get; set; }

        [Required(ErrorMessage = "Learn path lesson number required")]
        public long LessonNumber { get; set; }

        [Required(ErrorMessage = "Learn path theory number required")]
        public long TheoryNumber { get; set; }

        [Required(ErrorMessage = "Learn path finish status required")]
        public bool Finished { get; set; }
    }
}
