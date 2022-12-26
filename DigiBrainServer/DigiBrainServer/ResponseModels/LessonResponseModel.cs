
namespace DigiBrainServer.ResponseModels
{
    public class LessonResponseModel
    {
        public long Id { get; set; }
        public string Title { get; set; }
        public string Text { get; set; }
        public long ChapterId { get; set; }
    }
}
