using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class LanguageViewModel
    {
        [Required(ErrorMessage = "Language name required")]
        public string Name { get; set; }

        [Required(ErrorMessage = "Code for language required")]
        public string Code { get; set; }
    }
}
