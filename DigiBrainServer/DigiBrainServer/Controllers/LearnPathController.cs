using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/learn-paths")]
    [ApiController]
    public class LearnPathController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly UserManager<UserModel> _userManager;

        public LearnPathController(AppDbContext context, UserManager<UserModel> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<PathLearnResponseModel>>> GetAllLearningPaths()
        {
            var learningPaths = await _context.PathLearn.ToListAsync();

            var responseLearningPaths = new List<PathLearnResponseModel>();
            foreach (var learningPath in learningPaths)
            {
                responseLearningPaths.Add((PathLearnResponseModel)learningPath);
            }

            return Ok(responseLearningPaths);
        }

        [HttpGet]
        [Route("{id}")]
        public async Task<ActionResult<PathLearnDetailsResponse>> GetLearningPathDetails(long id)
        {
            var learningPath = await _context.PathLearn.FindAsync(id);
            if (learningPath == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learning path does not exist"
                });
            }

            var responseSections = new List<PathLearnSectionDetailsResponse>();
            var sections = await _context.PathSection.Where(item => item.PathLearnId == id).ToListAsync();
            
            foreach(var section in sections)
            {
                var responseLessons = new List<PathLearnLessonDetailsResponse>();
                var lessons = await _context.PathLesson.Where(item => item.PathSectionId == section.Id).ToListAsync();

                foreach(var lesson in lessons)
                {
                    var quizzes = await _context.PathLessonQuiz.Where(item => item.PathLessonId == lesson.Id).ToListAsync();
                    var responseQuizzes = new List<PathLessonResponseQuiz>();
                    foreach(var quiz in quizzes)
                    {
                        responseQuizzes.Add((PathLessonResponseQuiz)quiz);
                    }

                    var theoryList = await _context.PathLessonTheory.Where(item => item.PathLessonId == lesson.Id).ToListAsync();
                    var responseTheory = new List<PathLessonResponseTheory>();
                    foreach (var theory in theoryList)
                    {
                        responseTheory.Add((PathLessonResponseTheory)theory);
                    }

                    responseLessons.Add(new PathLearnLessonDetailsResponse((PathLessonResponseModel)lesson, responseQuizzes, responseTheory));
                }

                responseSections.Add(new PathLearnSectionDetailsResponse((PathSectionResponseModel)section, responseLessons));
            }

            return Ok(new PathLearnDetailsResponse((PathLearnResponseModel)learningPath, responseSections));
        }

        [HttpGet]
        [Route("users/{username}/status")]
        public async Task<ActionResult<IEnumerable<PathLearnStatusResponseModel>>> GetStatusForUser(string username)
        {
            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }

            var learnPathStatusList = await _context.PathLearnStatus.Where(item => item.Username == username).ToListAsync();

            var learnPathStatusResponseList = new List<PathLearnStatusResponseModel>();
            foreach(var learnPath in learnPathStatusList)
            {
                var quizStatuses = await _context.PathLearnQuizStatus.Where(item => item.Username == username && 
                                                                                    item.PathLearnId == learnPath.PathLearnId).ToListAsync();
                
                var response = new PathLearnStatusResponseModel(learnPath);
                foreach(var quizStatus in quizStatuses)
                {
                    response.AddQuiz(quizStatus);
                }

                learnPathStatusResponseList.Add(response);
            }

            return Ok(learnPathStatusResponseList);
        }

        [HttpPost]
        [Route("{id}/users/{username}/status")]
        [Authorize(Roles = "admin,student,teacher")]
        public async Task<ActionResult<PathLearnStatusModel>> AddLearnPathStatus(long id, string username)
        {
            var learnPath = await _context.PathLearn.FindAsync(id);
            if (learnPath == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learning path does not exist"
                });
            }
            learnPath.Started++;    // Increase the number of users that started this learning path

            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }

            var currentUser = await _userManager.FindByNameAsync(User.Identity.Name);
            if (currentUser.Id != user.Id)
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Not allowed to modify this user's data"
                });
            }

            var learnPathStatus = await _context.PathLearnStatus.Where(item => item.Username == username && item.PathLearnId == id).FirstOrDefaultAsync();
            if(learnPathStatus != null)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "Status already exists for this user and learn path"
                });
            }

            long newId = _context.PathLearnStatus.Count() + 1;
            var modelCheck = await _context.PathLearnStatus.FindAsync(newId);
            while (modelCheck != null)
            {
                newId++;
                modelCheck = await _context.PathLearnStatus.FindAsync(newId);
            }

            PathLearnStatusModel model = new(newId, username, id, 1, 1, 1, false);
            _context.PathLearnStatus.Add(model);
            _context.PathLearn.Update(learnPath);
            await _context.SaveChangesAsync();

            return Ok(model);
        }

        [HttpPut]
        [Route("{id}/users/{username}/status")]
        [Authorize(Roles = "admin,student,teacher")]
        public async Task<ActionResult<PathLearnStatusModel>> UpdateLearnPathStatus(long id, string username, PathLearnStatusViewModel model)
        {
            var learnPath = await _context.PathLearn.FindAsync(id);
            if (learnPath == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learning path does not exist"
                });
            }

            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }

            var currentUser = await _userManager.FindByNameAsync(User.Identity.Name);
            if (currentUser.Id != user.Id)
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Not allowed to modify this user's data"
                });
            }

            var learnPathStatus = await _context.PathLearnStatus.Where(item => item.PathLearnId == id).FirstOrDefaultAsync();
            if (learnPathStatus == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "No status found for this learn  path"
                });
            }

            learnPathStatus.SectionNumber = model.SectionNumber;
            learnPathStatus.LessonNumber = model.LessonNumber;
            learnPathStatus.TheoryNumber = model.TheoryNumber;
            learnPathStatus.Finished = model.Finished;

            _context.PathLearnStatus.Update(learnPathStatus);
            await _context.SaveChangesAsync();

            return Ok(learnPathStatus);
        }

        [HttpPut]
        [Route("{id}/users/{username}/status/quiz")]
        [Authorize(Roles = "admin,student,teacher")]
        public async Task<ActionResult<PathLearnStatusModel>> UpdateLearnPathQuizStatus(long id, string username, PathLearnQuizStatusViewModel model)
        {
            var learnPath = await _context.PathLearn.FindAsync(id);
            if (learnPath == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learning path does not exist"
                });
            }

            var user = await _userManager.FindByNameAsync(username);
            if (user == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This used does not exist"
                });
            }

            var currentUser = await _userManager.FindByNameAsync(User.Identity.Name);
            if (currentUser.Id != user.Id)
            {
                return Unauthorized(new ErrorResponseModel
                {
                    Message = "Not allowed to modify this user's data"
                });
            }

            var learnPathStatus = await _context.PathLearnStatus.Where(item => item.PathLearnId == id).FirstOrDefaultAsync();
            if (learnPathStatus == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "No status found for this learn  path"
                });
            }

            var quizStatusModel = await _context.PathLearnQuizStatus.Where(item => item.Username.Equals(username) &&
                                                                                    item.SectionNumber == model.SectionNumber &&
                                                                                    item.LessonNumber == model.LessonNumber &&
                                                                                    item.PathLearnId == id).FirstOrDefaultAsync();
            if(quizStatusModel != null)
            {
                quizStatusModel.Score = model.Score;
                _context.PathLearnQuizStatus.Update(quizStatusModel);
                await _context.SaveChangesAsync();

                return Ok(quizStatusModel);
            }

            long quizStatusId = _context.PathLearnQuizStatus.Count() + 1;
            var quizStatusCheck = await _context.PathLearnQuizStatus.FindAsync(quizStatusId);
            while(quizStatusCheck != null)
            {
                quizStatusId++;
                quizStatusCheck = await _context.PathLearnQuizStatus.FindAsync(quizStatusId);
            }

            quizStatusModel = new(quizStatusId, username, id, model.SectionNumber, model.LessonNumber, model.Score);
            _context.PathLearnQuizStatus.Add(quizStatusModel);
            await _context.SaveChangesAsync();

            return Ok(quizStatusModel);
        }

        [HttpPost]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLearnResponseModel>> AddLearnPath(PathLearnViewModel model)
        {
            var modelCheck = _context.PathLearn.Where(item => item.Title.Equals(model.Title)).FirstOrDefault();
            if(modelCheck != null)
            {
                return BadRequest(new { message = "Learn path with this name already exists" });
            }

            long id = _context.PathLearn.Count() + 1;
            modelCheck = await _context.PathLearn.FindAsync(id);
            while (modelCheck != null)
            {
                id++;
                modelCheck = await _context.PathLearn.FindAsync(id);
            }

            PathLearnModel learnPath = new(id, model.Title, model.Description, model.Author, DateTime.Now, 0, model.SubjectId, model.ImageName);
            _context.PathLearn.Add(learnPath);
            await _context.SaveChangesAsync();

            return Ok((PathLearnResponseModel)learnPath);
        }

        [HttpPost]
        [Route("sections")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathSectionResponseModel>> AddLearnPathSection(PathSectionViewModel model)
        {
            var modelCheck = _context.PathSection.Where(item => item.PathLearnId == model.PathLearnId && ( item.Title.Equals(model.Title) || item.Number == model.Number )).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path section with this name or number already exists" });
            }

            long id = _context.PathSection.Count() + 1;
            modelCheck = await _context.PathSection.FindAsync(id);
            while (modelCheck != null)
            {
                id++;
                modelCheck = await _context.PathSection.FindAsync(id);
            }

            PathSectionModel learnPathSection = new(id, model.Number, model.Title, model.IconId, model.PathLearnId);
            _context.PathSection.Add(learnPathSection);
            await _context.SaveChangesAsync();

            return Ok((PathSectionResponseModel)learnPathSection);
        }

        [HttpPost]
        [Route("sections/lessons")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLessonResponseModel>> AddLearnPathLesson(PathLessonViewModel model)
        {
            var modelCheck = _context.PathLesson.Where(item => item.PathSectionId == model.PathSectionId && ( item.Title.Equals(model.Title) || item.Number == model.Number )).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path lesson with this name or number already exists" });
            }

            long id = _context.PathLesson.Count() + 1;
            modelCheck = await _context.PathLesson.FindAsync(id);
            while (modelCheck != null)
            {
                id++;
                modelCheck = await _context.PathLesson.FindAsync(id);
            }

            PathLessonModel learnPathLesson = new(id, model.Number, model.Title, model.Description, model.PathSectionId);
            _context.PathLesson.Add(learnPathLesson);
            await _context.SaveChangesAsync();

            return Ok((PathLessonResponseModel)learnPathLesson);
        }

        [HttpPost]
        [Route("sections/lessons/quiz")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult> AddLearnPathLessonQuiz(List<PathLessonQuizViewModel> questions)
        {
            foreach (var model in questions)
            {
                var modelCheck = _context.PathLessonQuiz.Where(item => item.PathLessonId == model.PathLessonId && item.QuestionId == model.QuestionId).FirstOrDefault();
                if (modelCheck != null)
                {
                    continue;
                }

                long id = _context.PathLessonQuiz.Count() + 1;
                modelCheck = await _context.PathLessonQuiz.FindAsync(id);
                while (modelCheck != null)
                {
                    id++;
                    modelCheck = await _context.PathLessonQuiz.FindAsync(id);
                }

                PathLessonQuiz learnPathLessonQuiz = new(id, model.Score, model.QuestionId, model.PathLessonId);
                _context.PathLessonQuiz.Add(learnPathLessonQuiz);
            }
            
            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpPost]
        [Route("sections/lessons/theory")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLessonResponseTheory>> AddLearnPathLessonTheory(PathLessonTheoryViewModel model)
        {
            var modelCheck = _context.PathLessonTheory.Where(item => item.PathLessonId == model.PathLessonId && item.Number == model.Number).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path lesson theory with this number already exists" });
            }

            long id = _context.PathLessonTheory.Count() + 1;
            modelCheck = await _context.PathLessonTheory.FindAsync(id);
            while (modelCheck != null)
            {
                id++;
                modelCheck = await _context.PathLessonTheory.FindAsync(id);
            }

            PathLessonTheory learnPathLessonTheory = new(id, model.Number, model.Title, model.Text, model.PathLessonId);
            _context.PathLessonTheory.Add(learnPathLessonTheory);
            await _context.SaveChangesAsync();

            return Ok((PathLessonResponseTheory)learnPathLessonTheory);
        }

        [HttpDelete]
        [Route("sections/{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathSectionResponseModel>> DeletePathLearnSection(long id)
        {
            var section = await _context.PathSection.FindAsync(id);
            if(section == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learn path section doesn't exist"
                });
            }

            _context.PathSection.Remove(section);
            await _context.SaveChangesAsync();

            return Ok((PathSectionResponseModel)section);
        }

        [HttpDelete]
        [Route("sections/lessons/{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLessonResponseModel>> DeletePathLearnLesson(long id)
        {
            var lesson = await _context.PathLesson.FindAsync(id);
            if (lesson == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learn path lesson doesn't exist"
                });
            }

            _context.PathLesson.Remove(lesson);
            await _context.SaveChangesAsync();

            return Ok((PathLessonResponseModel)lesson);
        }

        [HttpDelete]
        [Route("sections/lessons/{id}/quiz")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult> DeletePathLearnLessonQuiz(long id)
        {
            var questions = await _context.PathLessonQuiz.Where(item => item.PathLessonId == id).ToListAsync();
            foreach(var question in questions)
            {
                _context.PathLessonQuiz.Remove(question);
            }
            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpDelete]
        [Route("sections/lessons/theory/{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLessonResponseTheory>> DeletePathLearnLessonTheory(long id)
        {
            var theory = await _context.PathLessonTheory.FindAsync(id);
            if (theory == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learn path lesson theory doesn't exist"
                });
            }

            _context.PathLessonTheory.Remove(theory);
            await _context.SaveChangesAsync();

            return Ok((PathLessonResponseTheory)theory);
        }

        [HttpPut]
        [Route("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLearnResponseModel>> UpdateLearnPath(long id, PathLearnViewModel model)
        {
            var learnPath = await _context.PathLearn.FindAsync(id);
            if (learnPath == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learning path does not exist"
                });
            }

            var modelCheck = _context.PathLearn.Where(item => item.Id != id && item.Title.Equals(model.Title)).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path with this name already exists" });
            }

            learnPath.Title = model.Title;
            learnPath.Description = model.Description;
            learnPath.Author = model.Author;
            learnPath.SubjectId = model.SubjectId;
            learnPath.ImageName = model.ImageName;

            _context.PathLearn.Update(learnPath);
            await _context.SaveChangesAsync();

            return Ok((PathLearnResponseModel)learnPath);
        }

        [HttpPut]
        [Route("sections/{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathSectionResponseModel>> UpdateLearnPathSection(long id, PathSectionViewModel model)
        {
            var section = await _context.PathSection.FindAsync(id);
            if (section == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learn path section doesn't exist"
                });
            }

            var modelCheck = _context.PathSection.Where(item => item.Id != id && item.PathLearnId == model.PathLearnId && (item.Title.Equals(model.Title) || item.Number == model.Number)).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path section with this name or number already exists" });
            }

            section.Number = model.Number;
            section.Title = model.Title;
            section.IconId = model.IconId;
            section.PathLearnId = model.PathLearnId;

            _context.PathSection.Update(section);
            await _context.SaveChangesAsync();

            return Ok((PathSectionResponseModel)section);
        }

        [HttpPut]
        [Route("sections/lessons/{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLessonResponseModel>> UpdateLearnPathLesson(long id, PathLessonViewModel model)
        {
            var lesson = await _context.PathLesson.FindAsync(id);
            if (lesson == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learn path lesson doesn't exist"
                });
            }

            var modelCheck = _context.PathLesson.Where(item => item.Id != id && item.PathSectionId == model.PathSectionId && (item.Title.Equals(model.Title) || item.Number == model.Number)).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path lesson with this name or number already exists" });
            }

            lesson.Number = model.Number;
            lesson.Title = model.Title;
            lesson.Description = model.Description;
            lesson.PathSectionId = model.PathSectionId;

            _context.PathLesson.Update(lesson);
            await _context.SaveChangesAsync();

            return Ok((PathLessonResponseModel)lesson);
        }

        [HttpPut]
        [Route("sections/lessons/{id}/quiz")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult> UpdateLearnPathLessonQuiz(long id, List<PathLessonQuizViewModel> questions)
        {
            var deletedQuestions = await _context.PathLessonQuiz.Where(item => item.PathLessonId == id).ToListAsync();
            foreach (var question in deletedQuestions)
            {
                _context.PathLessonQuiz.Remove(question);
            }

            await _context.SaveChangesAsync();

            foreach (var model in questions)
            {
                var modelCheck = _context.PathLessonQuiz.Where(item => item.PathLessonId == model.PathLessonId && item.QuestionId == model.QuestionId).FirstOrDefault();
                if (modelCheck != null)
                {
                    continue;
                }

                long newId = _context.PathLessonQuiz.Count() + 1;
                modelCheck = await _context.PathLessonQuiz.FindAsync(newId);
                while (modelCheck != null)
                {
                    newId++;
                    modelCheck = await _context.PathLessonQuiz.FindAsync(newId);
                }

                PathLessonQuiz learnPathLessonQuiz = new(newId, model.Score, model.QuestionId, model.PathLessonId);
                _context.PathLessonQuiz.Add(learnPathLessonQuiz);
            }

            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpPut]
        [Route("sections/lessons/theory/{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<PathLessonResponseTheory>> UpdateLearnPathLessonTheory(long id, PathLessonTheoryViewModel model)
        {
            var theory = await _context.PathLessonTheory.FindAsync(id);
            if (theory == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This learn path lesson theory doesn't exist"
                });
            }

            var modelCheck = _context.PathLessonTheory.Where(item => item.Id != id && item.PathLessonId == model.PathLessonId && item.Number == model.Number).FirstOrDefault();
            if (modelCheck != null)
            {
                return BadRequest(new { message = "Learn path lesson theory with this number already exists" });
            }

            theory.Number = model.Number;
            theory.Title = model.Title;
            theory.Text = model.Text;
            theory.PathLessonId = model.PathLessonId;

            _context.PathLessonTheory.Update(theory);
            await _context.SaveChangesAsync();

            return Ok((PathLessonResponseTheory)theory);
        }
    }
}
