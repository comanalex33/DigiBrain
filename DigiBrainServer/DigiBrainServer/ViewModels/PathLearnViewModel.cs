using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathLearnViewModel
    {
        [Required(ErrorMessage = "Learn path title required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Learn path description required")]
        public string Description { get; set; }

        [Required(ErrorMessage = "Learn path author required")]
        public string Author { get; set; }

        [Required(ErrorMessage = "Learn path subject required")]
        public long SubjectId { get; set; }

        public string ImageName { get; set; }
    }
}
