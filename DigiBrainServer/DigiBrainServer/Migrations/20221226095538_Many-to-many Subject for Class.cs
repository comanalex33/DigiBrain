using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace DigiBrainServer.Migrations
{
    public partial class ManytomanySubjectforClass : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ClassId",
                table: "Subject");

            migrationBuilder.CreateTable(
                name: "ClassSubject",
                columns: table => new
                {
                    Id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    ClassId = table.Column<long>(type: "bigint", nullable: false),
                    SubjectId = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ClassSubject", x => x.Id);
                });
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ClassSubject");

            migrationBuilder.AddColumn<long>(
                name: "ClassId",
                table: "Subject",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);
        }
    }
}
