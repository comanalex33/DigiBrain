using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLessonTheoryViewModel
    {
        [Required(ErrorMessage = "Learn path lesson theory number required")]
        public long Number { get; set; }

        [Required(ErrorMessage = "Learn path lesson theory title required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Learn path lesson theory text required")]
        public string Text { get; set; }

        [Required(ErrorMessage = "Learn path lesson theory parent required")]
        public long PathLessonId { get; set; }
    }
}
