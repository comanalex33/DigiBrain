using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/quizes")]
    [ApiController]
    public class QuizController : ControllerBase
    {
        public readonly AppDbContext _context;

        public QuizController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Route("{quizId}/questions")]
        public async Task<ActionResult<IEnumerable<QuestionResponseModel>>> GetQuizQuestions(long quizId)
        {
            var questionIds = await _context.QuizQuestion.Where(item => item.QuizId == quizId).Select(item => item.QuestionId).ToListAsync();
            
            var responseQuestions = new List<QuestionResponseModel>();
            foreach(var questionId in questionIds)
            {
                var question = await _context.Question.FindAsync(questionId);
                if(question == null)
                {
                    return NotFound(new ErrorResponseModel
                    {
                        Message = "Question not found"
                    });
                }
                responseQuestions.Add((QuestionResponseModel)question);
            }

            return Ok(responseQuestions);
        }

        [HttpGet]
        [Route("users/{username}")]
        public async Task<ActionResult<IEnumerable<QuizModel>>> GetUserQuizes(string username)
        {
            return await _context.Quiz.Where(item => item.Username.Equals(username)).ToListAsync();
        }

        [HttpPost]
        [Route("users/{username}")]
        public async Task<ActionResult<QuizModel>> CreateQuiz(string username)
        {
            long id = _context.Quiz.Count() + 1;
            var quizCheck = await _context.Quiz.FindAsync(id);
            while (quizCheck != null)
            {
                id++;
                quizCheck = await _context.Quiz.FindAsync(id);
            }

            QuizModel quiz = new(id, DateTime.Now, username);
            _context.Quiz.Add(quiz);
            await _context.SaveChangesAsync();

            return Ok(quiz);
        }

        [HttpPost]
        public async Task<ActionResult<QuizQuestionModel>> AddQuestionToQuiz(QuizQuestionViewModel model)
        {
            // Check quiz existance
            var quizCheck = await _context.Quiz.FindAsync(model.Id);
            while (quizCheck == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Quiz not found"
                });
            }

            // Check question existance
            var questionCheck = await _context.Question.FindAsync(model.QuestionId);
            while (questionCheck == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Question not found"
                });
            }

            // Check answer existance
            var answerCheck = await _context.Answer.FindAsync(model.AnswerId);
            while (answerCheck == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Answer not found"
                });
            }

            // Add question to quiz
            long id = _context.QuizQuestion.Count() + 1;
            var quizQuestionCheck = await _context.QuizQuestion.FindAsync(id);
            while (quizQuestionCheck != null)
            {
                id++;
                quizQuestionCheck = await _context.QuizQuestion.FindAsync(id);
            }

            QuizQuestionModel quizQuestion = new(id, model.Id, model.QuestionId, model.AnswerId);
            _context.QuizQuestion.Add(quizQuestion);
            await _context.SaveChangesAsync();

            return Ok(quizQuestion);
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult<QuizModel>> DeleteQuiz(long id)
        {
            var quiz = await _context.Quiz.FindAsync(id);
            if (quiz == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This quiz does not exist"
                });
            }

            _context.Quiz.Remove(quiz);
            await _context.SaveChangesAsync();

            return Ok(quiz);
        }
    }
}
