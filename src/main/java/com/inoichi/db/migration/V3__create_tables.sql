-- Drop the entire app schema if it exists
DROP SCHEMA IF EXISTS app CASCADE;

-- Recreate the app schema
CREATE SCHEMA app;
-- Create Houses Table in app schema
CREATE TABLE app.houses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    purpose TEXT NOT NULL
);

-- Create Teams Table in app schema
CREATE TABLE app.teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    house_id UUID NOT NULL,
    FOREIGN KEY (house_id) REFERENCES app.houses(id) ON DELETE CASCADE
);

-- Create Users Table in app schema
CREATE TABLE app.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Create User_Team Mapping Table in app schema
CREATE TABLE app.user_teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    team_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES app.users(id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES app.teams(id) ON DELETE CASCADE,
    UNIQUE (user_id, team_id)
);

-- Insert Houses
INSERT INTO app.houses (id, name, purpose) VALUES
    (gen_random_uuid(), 'Gryffindor', 'Tree Planting and Reforestation'),
    (gen_random_uuid(), 'Slytherin', 'Trash Cleanup and Waste Management'),
    (gen_random_uuid(), 'Ravenclaw', 'Public Transport and Carbon Reduction');

-- Insert Teams
INSERT INTO app.teams (id, name, house_id) VALUES
    -- Gryffindor (Tree Planting Teams)
    (gen_random_uuid(), 'Forest Guardians', (SELECT id FROM app.houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Whomping Willow Growers', (SELECT id FROM app.houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Green Saplings', (SELECT id FROM app.houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Tree Huggers', (SELECT id FROM app.houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Eco Warriors', (SELECT id FROM app.houses WHERE name = 'Gryffindor')),

    -- Slytherin (Trash Cleanup Teams)
    (gen_random_uuid(), 'Trash Troopers', (SELECT id FROM app.houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Litter Avengers', (SELECT id FROM app.houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Waste Wizards', (SELECT id FROM app.houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Recycling Snakes', (SELECT id FROM app.houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Eco Serpents', (SELECT id FROM app.houses WHERE name = 'Slytherin')),

    -- Ravenclaw (Public Transport Teams)
    (gen_random_uuid(), 'Carbon Busters', (SELECT id FROM app.houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Green Commuters', (SELECT id FROM app.houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Broomstick Riders', (SELECT id FROM app.houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Eco Navigators', (SELECT id FROM app.houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Sustainable Voyagers', (SELECT id FROM app.houses WHERE name = 'Ravenclaw'));