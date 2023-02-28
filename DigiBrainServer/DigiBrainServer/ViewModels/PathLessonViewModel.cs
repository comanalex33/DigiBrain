using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLessonViewModel
    {
        [Required(ErrorMessage = "Learn path lesson number required")]
        public long Number { get; set; }

        [Required(ErrorMessage = "Learn path lesson title required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Learn path lesson description required")]
        public string Description { get; set; }

        [Required(ErrorMessage = "Learn path lesson parent required")]
        public long PathSectionId { get; set; }
    }
}
