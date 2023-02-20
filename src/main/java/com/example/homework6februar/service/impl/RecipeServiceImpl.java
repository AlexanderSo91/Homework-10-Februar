package com.example.homework6februar.service.impl;

import com.example.homework6februar.service.FileService;
import com.example.homework6februar.service.RecipeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.homework6februar.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import com.example.homework6februar.model.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.homework6februar.service.ValidationService;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private static long idCounter = 1;
    private Map<Long, Recipe> recipes = new HashMap<>();
    private final ValidationService validationService;
    private final FileService fileService;

    @Value("${path.to.recipes.file}")
    private String recipesFilePath;

    @Value("${name.of.recipes.file}")
    private String recipesFileName;

    private Path recipesPath;


    @Override
    public Recipe save(Recipe recipe) {
        if (!validationService.validate(recipe))
            throw new ValidationException(recipe.toString());

        recipes.put(idCounter++, recipe);
        fileService.saveMapToFile(recipes,recipesPath);

        return recipe;
    }

    @Override
    public Optional<Recipe> getById(Long id) {
        return Optional.ofNullable(recipes.get(id));
    }

    @Override
    public Recipe update(Long id, Recipe recipe) {
        if (!validationService.validate(recipe))
            throw new ValidationException(recipe.toString());

        recipes.replace(id, recipe);
        fileService.saveMapToFile(recipes,recipesPath);

        return recipe;
    }

    @Override
    public Recipe delete(Long id) {
        Recipe recipe = recipes.remove(id);
        fileService.saveMapToFile(recipes,recipesPath);
        return recipe;
    }

    @Override
    public Map<Long, Recipe> getAll() {
        return recipes;
    }

    @PostConstruct
    private void init() {
        recipesPath = Path.of(recipesFilePath,recipesFileName);
        recipes = fileService.readMapFromFile(recipesPath, new TypeReference<HashMap<Long, Recipe>>() {});

    }
}
