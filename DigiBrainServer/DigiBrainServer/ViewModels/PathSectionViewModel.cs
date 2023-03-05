using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class PathSectionViewModel
    {
        [Required(ErrorMessage = "Learn path section number required")]
        public long Number { get; set; }

        [Required(ErrorMessage = "Learn path section title required")]
        public string Title { get; set; }

        public long IconId { get; set; }

        [Required(ErrorMessage = "Learn path section parent required")]
        public long PathLearnId { get; set; }
    }
}
