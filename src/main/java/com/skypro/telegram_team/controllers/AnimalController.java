package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Cat;
import com.skypro.telegram_team.models.Dog;
import com.skypro.telegram_team.services.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Operation(summary = "поиск животного в БД по личному идентификатору", tags = "Animals"
            , responses = {@ApiResponse(
            responseCode = "200",
            description = "Найденное животное",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Animal.class)
            )
    ), @ApiResponse(responseCode = "404",
            description = "Животное не найдено",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 404,
                              "error": "Bad Request",
                              "path": "/animals/{id}"
                            }""")
            )
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 400,
                              "error": "Bad Request",
                              "path": "/animals/{id}"
                            }""")
            )
    ), @ApiResponse(
            responseCode = "500",
            description = "Проблемы на стороне сервера",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:22:05.642+00:00",
                              "status": 500,
                              "error": "Internal Server Error",
                              "path": "/animals/{id}"
                            }""")
            )
    )
    })
    @GetMapping("/{id}")
    public Animal findById(@Parameter(description = "Идентификатор животного") @PathVariable long id) {
        return animalService.findById(id);
    }

    @Operation(summary = "поиск животного в БД по имени", tags = "Animals")
    @GetMapping("/name")
    public List<Animal> findByName(@RequestParam String name) {
        return animalService.findByName(name);
    } //

    @Operation(summary = "Получение списка всех животных из БД"
            , tags = "Animals"
            , responses = {@ApiResponse(
            responseCode = "200",
            description = "Список всех животных из БД",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))
            )
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 400,
                              "error": "Bad Request",
                              "path": "/animals/"
                            }""")
            )
    ), @ApiResponse(
            responseCode = "500",
            description = "Проблемы на стороне сервера",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 500,
                              "error": "Internal Server Error",
                              "path": "/animals/"
                            }""")
            )
    )
    })
    @GetMapping
    public Iterable<Animal> getAllAnimals() {
        return animalService.findAll();
    }

    @Operation(summary = "Удаление животного из БД по личному идентификатору"
            , tags = "Animals"
            , responses = {@ApiResponse(
            responseCode = "200",
            description = "Удаленное из БД животное",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Animal.class)
            )
    ), @ApiResponse(responseCode = "404",
            description = "Животное по указанному идентификатору не найдено",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE
                    , schema = @Schema(example = """
                    {
                      "timestamp": "2023-04-02T16:20:14.430+00:00",
                      "status": 404,
                      "error": "Bad Request",
                      "path": "/animals/{id}"
                    }"""))
    ), @ApiResponse(
            responseCode = "400",
            description = "Неверный запрос",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 400,
                              "error": "Bad Request",
                              "path": "/animals/{id}"
                            }""")
            )
    ), @ApiResponse(
            responseCode = "500",
            description = "Проблемы на стороне сервера",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 500,
                              "error": "Internal Server Error",
                              "path": "/animals/{id}"
                            }""")
            )
    )
    })
    @DeleteMapping("/{id}")
    public Animal deleteAnimal(@Parameter(description = "Идентификатор животного", example = "1") @PathVariable long id) {
        return animalService.deleteById(id);
    }

    @Operation(
            summary = "Изменение данных о животном по личному идентификатору"
            , tags = "Cats",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ввод новых данных о животном",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class)
                    )
            ),
            responses = {@ApiResponse(responseCode = "200"
                    , description = "Животное с измененными данными",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class))
            ), @ApiResponse(responseCode = "404"
                    , description = "Животное по данному идентификатору не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE
                            , schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 404,
                              "error": "Bad Request",
                              "path": "/animals/{id}"
                            }"""))
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Неверный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2023-04-02T16:20:14.430+00:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "path": "/animals/{id}"
                                    }""")
                    )
            ), @ApiResponse(
                    responseCode = "500",
                    description = "Проблемы на стороне сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2023-04-02T16:20:14.430+00:00",
                                      "status": 500,
                                      "error": "Internal Server Error",
                                      "path": "/animals/{id}"
                                    }""")
                    )
            )}
    )
    @PutMapping("/cats/{id}")
    public Animal updateCat(@RequestBody Cat cat,
                               @Parameter(description = "Личный идентификатор кошки", example = "1") @PathVariable Long id) {
        return animalService.update(cat, id);
    }

    @Operation(
            summary = "Изменение данных о животном по личному идентификатору"
            , tags = "Dogs",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ввод новых данных о животном",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class)
                    )
            ),
            responses = {@ApiResponse(responseCode = "200"
                    , description = "Животное с измененными данными",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class))
            ), @ApiResponse(responseCode = "404"
                    , description = "Животное по данному идентификатору не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE
                            , schema = @Schema(example = """
                            {
                              "timestamp": "2023-04-02T16:20:14.430+00:00",
                              "status": 404,
                              "error": "Bad Request",
                              "path": "/dogs/{id}"
                            }"""))
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Неверный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2023-04-02T16:20:14.430+00:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "path": "/dogs/{id}"
                                    }""")
                    )
            ), @ApiResponse(
                    responseCode = "500",
                    description = "Проблемы на стороне сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2023-04-02T16:20:14.430+00:00",
                                      "status": 500,
                                      "error": "Internal Server Error",
                                      "path": "/dogs/{id}"
                                    }""")
                    )
            )}
    )
    @PutMapping("/dogs/{id}")
    public Animal updateDog(@RequestBody Dog dog,
                            @Parameter(description = "Личный идентификатор собаки", example = "1") @PathVariable Long id) {
        return animalService.update(dog, id);
    }

    @Operation(summary = "Получение всех кошек из БД.", tags = "Cats")
    @GetMapping("/cats")
    public List<Cat> getAllCats() {
        return animalService.getAllCats();
    }

    @Operation(summary = "Получение всех собак из БД.", tags = "Dogs")
    @GetMapping("/dogs")
    public List<Dog> getAllDogs() {
        return animalService.getAllDogs();
    }

    @Operation(summary = "Создание кошки.", tags = "Cats")
    @PostMapping("/cats")
    public Animal createCat(@RequestBody Cat cat) {
        return animalService.createCat(cat);
    }

    @Operation(summary = "Создание собаки.", tags = "Dogs")
    @PostMapping("/dogs")
    public Animal createDog(@RequestBody Dog dog) {
        return animalService.createDog(dog);
    }

}
