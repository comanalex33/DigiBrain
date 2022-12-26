using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class DomainViewModel
    {
        [Required(ErrorMessage = "Domain name required")]
        public string Name { get; set; }

        [Required(ErrorMessage = "Language required")]
        public long LanguageId { get; set; }

        [Required(ErrorMessage = "Icon required")]
        public long IconId { get; set; }
    }
}
