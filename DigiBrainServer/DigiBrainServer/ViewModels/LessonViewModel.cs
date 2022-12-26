using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class LessonViewModel
    {
        [Required(ErrorMessage = "Lesson title required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Lesson text required")]
        public string Text { get; set; }

        [Required(ErrorMessage = "Chapter required")]
        public long ChapterId { get; set; }
    }
}
