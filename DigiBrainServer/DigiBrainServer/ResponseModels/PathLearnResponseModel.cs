using System;

namespace DigiBrainServer.ResponseModels
{
    public class PathLearnResponseModel
    {
        public long Id { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public string Author { get; set; }
        public DateTime Date { get; set; }
        public long SubjectId { get; set; }
        public string ImageName { get; set; }
    }
}
