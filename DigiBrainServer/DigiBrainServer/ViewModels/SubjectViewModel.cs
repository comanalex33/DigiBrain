using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class SubjectViewModel
    {
        [Required(ErrorMessage = "Subject name required")]
        public string Name { get; set; }

        [Required(ErrorMessage = "Class required")]
        public long ClassId { get; set; }

        [Required(ErrorMessage = "Icon required")]
        public long IconId { get; set; }
    }
}
