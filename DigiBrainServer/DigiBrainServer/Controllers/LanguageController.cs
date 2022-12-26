using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/languages")]
    [ApiController]
    public class LanguageController : ControllerBase
    {
        private readonly AppDbContext _context;

        public LanguageController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<LanguageResponseModel>>> GetAll()
        {
            var languages = await _context.Language.ToListAsync();

            var responseLanguages = new List<LanguageResponseModel>();
            foreach(var language in languages)
            {
                responseLanguages.Add((LanguageResponseModel)language);
            }

            return Ok(responseLanguages);
        }

        [HttpPost]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<LanguageResponseModel>> AddLanguage(LanguageViewModel languageModel)
        {
            var languageCheck = _context.Language.Where(item => item.Name.Equals(languageModel.Name) || item.Code.Equals(languageModel.Code)).FirstOrDefault();
            if (languageCheck != null)
            {
                return BadRequest(new { message = "Language already exists" });
            }

            long id = _context.Language.Count() + 1;
            languageCheck = await _context.Language.FindAsync(id);
            while (languageCheck != null)
            {
                id++;
                languageCheck = await _context.Language.FindAsync(id);
            }

            LanguageModel language = new(id, languageModel.Name, languageModel.Code);
            _context.Language.Add(language);
            await _context.SaveChangesAsync();

            return Ok((LanguageResponseModel)language);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<LanguageResponseModel>> DeleteLanguage(long id)
        {
            var language = await _context.Language.FindAsync(id);
            if (language == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This language does not exist"
                });
            }

            _context.Language.Remove(language);
            await _context.SaveChangesAsync();

            return Ok((LanguageResponseModel)language);
        }
    }
}