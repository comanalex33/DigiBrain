using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/reports")]
    [ApiController]
    public class QuizReportController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly UserManager<UserModel> _userManager;
        private readonly List<string> acceptedTypes = new()
        {
            "MultipleChoice",
            "WordsGap",
            "TrueFalse"
        };
        private readonly List<string> acceptedDifficulties = new()
        {
            "Easy",
            "Medium",
            "Hard"
        };

        public QuizReportController(AppDbContext context, UserManager<UserModel> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        [HttpGet]
        [Route("users/{username}")]
        public async Task<ActionResult<IEnumerable<QuizReportModel>>> GetUserReports(string username)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "User does not exists"
                });
            }

            var reports = await _context.QuizReport.Where(item => item.Username.Equals(username)).ToListAsync();
            return Ok(reports);
        }

        [HttpPost]
        public async Task<ActionResult<QuizReportModel>> AddQuizReport(QuizReportViewModel model)
        {
            var user = await _userManager.FindByNameAsync(model.Username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "User does not exists"
                });
            }
            var subject = await _context.Subject.FindAsync(model.SubjectId);
            if (subject == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Subject does not exists"
                });
            }

            // Check type correctness
            if (!acceptedTypes.Contains(model.QuizType))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Type not allowed, accepted types are " + string.Join(",", acceptedTypes)
                });
            }
            // Check difficulty correctness
            if (!acceptedDifficulties.Contains(model.Difficulty))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Difficulty not allowed, accepted difficulties are " + string.Join(",", acceptedDifficulties)
                });
            }

            long id = _context.QuizReport.Count() + 1;
            var reportCheck = await _context.QuizReport.FindAsync(id);
            while (reportCheck != null)
            {
                id++;
                reportCheck = await _context.QuizReport.FindAsync(id);
            }

            QuizReportModel report = new(id, model);
            _context.QuizReport.Add(report);
            await _context.SaveChangesAsync();

            return Ok(report);
        }
    }
}
