using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/questions")]
    [ApiController]
    public class QuestionController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly List<string> acceptedTypes = new()
        {
            "MultipleChoice",
            "WordsGap",
            "WrittenSolution"
        };
        private readonly List<string> acceptedDifficulties = new()
        {
            "Easy",
            "Medium",
            "Hard"
        };
        public QuestionController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<QuestionResponseModel>>> GetRandomQuestions([FromQuery]QuestionGeneratorViewModel model)
        {
            // Check type correctness
            if(!acceptedTypes.Contains(model.Type))
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
            if (GetNumberOfValidQuestions(model.Difficulty, model.Type, model.LanguageId) < model.Number)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Doesn't exist so much questions in the databse"
                });
            }

            var questions = new List<QuestionResponseModel>();
            var selectedQuestionsIds = new List<long>();

            // Get questions Ids
            var questionsIds = await _context.Question.Select(item => item.Id).ToListAsync();

            Random rnd = new();
            for(int number = 1; number <= model.Number; number++)
            {
                while(true)
                {
                    // Generate questionId
                    var questionIdPosition = rnd.Next(0, questionsIds.Count);
                    if (selectedQuestionsIds.Contains(questionIdPosition))
                    {
                        continue;
                    }
                    var questionId = questionsIds[questionIdPosition];

                    // Get and check question
                    var question = await _context.Question.FindAsync(questionId);
                    if(question.Type.Equals(model.Type) && question.Difficulty.Equals(model.Difficulty) && question.LanguageId == model.LanguageId)
                    {
                        selectedQuestionsIds.Add(questionIdPosition);
                        questions.Add((QuestionResponseModel)question);
                        break;
                    }
                }
            }

            return Ok(questions);
        }

        [HttpPost]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<QuestionResponseModel>> AddQuestion(QuestionViewModel model)
        {
            // Check type correctness
            if (!acceptedTypes.Contains(model.Type))
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

            var questionCheck = _context.Question.Where(item => item.Text == model.Text && item.Difficulty.Equals(model.Difficulty) && item.Type.Equals(model.Type)).FirstOrDefault();
            if (questionCheck != null)
            {
                return BadRequest(new { message = "A Question with this text, difficulty and type already exists" });
            }

            long id = _context.Question.Count() + 1;
            questionCheck = await _context.Question.FindAsync(id);
            while (questionCheck != null)
            {
                id++;
                questionCheck = await _context.Question.FindAsync(id);
            }

            QuestionModel question = new(id, model.Type, model.Difficulty, model.Text, model.LanguageId);
            _context.Question.Add(question);
            await _context.SaveChangesAsync();

            return Ok((QuestionResponseModel)question);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<QuestionResponseModel>> DeleteQuestion(long id)
        {
            var question = await _context.Question.FindAsync(id);
            if (question == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This question does not exist"
                });
            }

            _context.Question.Remove(question);
            await _context.SaveChangesAsync();

            return Ok((QuestionResponseModel)question);
        }

        private int GetNumberOfValidQuestions(string Difficulty, string Type, long LanguageId)
        {
            return _context.Question.Where(item => item.Difficulty.Equals(Difficulty) && item.Type.Equals(Type) && item.LanguageId == LanguageId).Count();
        }
    }
}
