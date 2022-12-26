using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class RegisterViewModel
    {
        [Required(ErrorMessage = "Username required")]
        public string Username { get; set; }

        [Required(ErrorMessage = "Password required")]
        public string Password { get; set; }

        [EmailAddress]
        [Required(ErrorMessage = "Email required")]
        public string Email { get; set; }
    }
}
