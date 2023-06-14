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
        [Authorize(Roles = "admin,teacher")]
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

        [HttpPost]
        [Route("multiple/")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<List<AnswerResponseModel>>> AddMultipleAnswer(List<AnswerViewModel> answers)
        {
            var addedAnswers = new List<AnswerResponseModel>();
            var index = 0;
            foreach (var model in answers)
            {
                var answerCheck = _context.Answer.Where(item => item.Text == model.Text && item.QuestionId == model.QuestionId && item.Position == model.Position).FirstOrDefault();
                if (answerCheck != null)
                {
                    return BadRequest(new { 
                        message = "An answer with this text already exists for this question: " + model.QuestionId.ToString(),
                        invalidFields = index.ToString()});
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
                addedAnswers.Add((AnswerResponseModel)answer);
                index++;
            }

            await _context.SaveChangesAsync();
            return Ok(addedAnswers);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin,teacher")]
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

        [HttpPut("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<AnswerResponseModel>> UpdateAnswer(long id, AnswerViewModel model)
        {
            var answer = await _context.Answer.FindAsync(id);
            if (answer == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This answer does not exist"
                });
            }

            var answerCheck = _context.Answer.Where(item => item.Id != id && item.Text == model.Text && item.QuestionId == model.QuestionId).FirstOrDefault();
            if (answerCheck != null)
            {
                return BadRequest(new { message = "An answer with this text already exists for this question" });
            }

            answer.Text = model.Text;
            answer.Position = model.Position;
            answer.Correct = model.Correct;
            answer.QuestionId = model.QuestionId;

            _context.Answer.Update(answer);
            await _context.SaveChangesAsync();

            return Ok((AnswerResponseModel)answer);
        }
    }
}
