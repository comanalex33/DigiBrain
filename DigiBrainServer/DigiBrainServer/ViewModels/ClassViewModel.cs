using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class ClassViewModel
    {
        [Required(ErrorMessage = "Class number required")]
        public int Number { get; set; }

        [Required(ErrorMessage = "If class at University required")]
        public bool AtUniversity { get; set; }

        [Required(ErrorMessage = "Domain required")]
        public long DomainId { get; set; }
    }
}
