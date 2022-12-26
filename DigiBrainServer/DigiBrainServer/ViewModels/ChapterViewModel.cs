using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class ChapterViewModel
    {
        [Required(ErrorMessage = "Chapter number required")]
        public int Number { get; set; }

        [Required(ErrorMessage = "Chapter name required")]
        public string Name { get; set; }

        [Required(ErrorMessage = "Subject required")]
        public long SubjectId { get; set; }
    }
}
