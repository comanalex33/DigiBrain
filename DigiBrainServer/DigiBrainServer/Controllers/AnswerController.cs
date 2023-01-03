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
    [Route("api/answers")]
    [ApiController]
    public class AnswerController : ControllerBase
    {
        private readonly AppDbContext _context;

        public AnswerController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Route("questions/{questionId}")]
        public async Task<ActionResult<IEnumerable<AnswerResponseModel>>> GetQuestionAnswers(long questionId)
        {
            var answers = await _context.Answer.Where(item => item.QuestionId == questionId).ToListAsync();
            var responseAnswers = new List<AnswerResponseModel>();
            foreach(var answer in answers)
            {
                responseAnswers.Add((AnswerResponseModel)answer);
            }

            return Ok(responseAnswers);
        }

        [HttpPost]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<AnswerResponseModel>> AddAnswer(AnswerViewModel model)
        {
            var answerCheck = _context.Answer.Where(item => item.Text == model.Text && item.QuestionId == model.QuestionId && item.Position == model.Position).FirstOrDefault();
            if (answerCheck != null)
            {
                return BadRequest(new { message = "An answer with this text already exists for this question" });
            }

            long id = _context.Answer.Count() + 1;
            answerCheck = await _context.Answer.FindAsync(id);
            while (answerCheck != null)
            {
                id++;
                answerCheck = await _context.Answer.FindAsync(id);
            }

            AnswerModel answer = new(id, model.Text, model.Position, model.Correct, model.QuestionId);
            _context.Answer.Add(answer);
            await _context.SaveChangesAsync();

            return Ok((AnswerResponseModel)answer);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<AnswerResponseModel>> DeleteAnswer(long id)
        {
            var answer = await _context.Answer.FindAsync(id);
            if (answer == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This answer does not exist"
                });
            }

            _context.Answer.Remove(answer);
            await _context.SaveChangesAsync();

            return Ok((AnswerResponseModel)answer);
        }
    }
}
