using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace DigiBrainServer.Migrations
{
    public partial class Languagetableadded : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "requestToResolve",
                table: "Domain",
                newName: "RequestToResolve");

            migrationBuilder.RenameColumn(
                name: "name",
                table: "Domain",
                newName: "Name");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "Domain",
                newName: "Id");

            migrationBuilder.RenameColumn(
                name: "requestToResolve",
                table: "Class",
                newName: "RequestToResolve");

            migrationBuilder.RenameColumn(
                name: "number",
                table: "Class",
                newName: "Number");

            migrationBuilder.RenameColumn(
                name: "domainId",
                table: "Class",
                newName: "DomainId");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "Class",
                newName: "Id");

            migrationBuilder.RenameColumn(
                name: "requestToResolve",
                table: "AspNetUsers",
                newName: "RequestToResolve");

            migrationBuilder.RenameColumn(
                name: "classId",
                table: "AspNetUsers",
                newName: "ClassId");

            migrationBuilder.AddColumn<long>(
                name: "LanguageId",
                table: "Domain",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);

            migrationBuilder.CreateTable(
                name: "Language",
                columns: table => new
                {
                    Id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "text", nullable: true),
                    Code = table.Column<string>(type: "text", nullable: true),
                    RequestToResolve = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Language", x => x.Id);
                });
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Language");

            migrationBuilder.DropColumn(
                name: "LanguageId",
                table: "Domain");

            migrationBuilder.RenameColumn(
                name: "RequestToResolve",
                table: "Domain",
                newName: "requestToResolve");

            migrationBuilder.RenameColumn(
                name: "Name",
                table: "Domain",
                newName: "name");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "Domain",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "RequestToResolve",
                table: "Class",
                newName: "requestToResolve");

            migrationBuilder.RenameColumn(
                name: "Number",
                table: "Class",
                newName: "number");

            migrationBuilder.RenameColumn(
                name: "DomainId",
                table: "Class",
                newName: "domainId");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "Class",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "RequestToResolve",
                table: "AspNetUsers",
                newName: "requestToResolve");

            migrationBuilder.RenameColumn(
                name: "ClassId",
                table: "AspNetUsers",
                newName: "classId");
        }
    }
}
