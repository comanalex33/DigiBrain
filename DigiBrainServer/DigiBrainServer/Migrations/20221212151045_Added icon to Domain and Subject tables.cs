using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace DigiBrainServer.Migrations
{
    public partial class AddedicontoDomainandSubjecttables : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<long>(
                name: "IconId",
                table: "Domain",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);

            migrationBuilder.CreateTable(
                name: "Subject",
                columns: table => new
                {
                    Id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "text", nullable: true),
                    ClassId = table.Column<long>(type: "bigint", nullable: false),
                    IconId = table.Column<long>(type: "bigint", nullable: false),
                    RequestToResolve = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Subject", x => x.Id);
                });
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Subject");

            migrationBuilder.DropColumn(
                name: "IconId",
                table: "Domain");
        }
    }
}
