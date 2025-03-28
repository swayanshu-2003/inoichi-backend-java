-- Create Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Create Houses Table (3 Houses with Specific Purposes)
CREATE TABLE houses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    purpose TEXT NOT NULL
);

-- Create Teams Table (Each House Has 5 Teams)
CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    house_id UUID NOT NULL,
    FOREIGN KEY (house_id) REFERENCES houses(id) ON DELETE CASCADE
);

-- Create User_Team Mapping Table (User Can Join 1 Team Per House)
CREATE TABLE user_teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    team_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    UNIQUE (user_id, team_id) -- Prevent duplicate team assignments
);

-- Insert Houses (3 Houses with Defined Environmental Goals)
INSERT INTO houses (id, name, purpose) VALUES
    (gen_random_uuid(), 'Gryffindor', 'Tree Planting and Reforestation'),
    (gen_random_uuid(), 'Slytherin', 'Trash Cleanup and Waste Management'),
    (gen_random_uuid(), 'Ravenclaw', 'Public Transport and Carbon Reduction');

-- Insert 5 Teams for Each House
INSERT INTO teams (id, name, house_id) VALUES
    -- Gryffindor (Tree Planting Teams)
    (gen_random_uuid(), 'Forest Guardians', (SELECT id FROM houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Whomping Willow Growers', (SELECT id FROM houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Green Saplings', (SELECT id FROM houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Tree Huggers', (SELECT id FROM houses WHERE name = 'Gryffindor')),
    (gen_random_uuid(), 'Eco Warriors', (SELECT id FROM houses WHERE name = 'Gryffindor')),

    -- Slytherin (Trash Cleanup Teams)
    (gen_random_uuid(), 'Trash Troopers', (SELECT id FROM houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Litter Avengers', (SELECT id FROM houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Waste Wizards', (SELECT id FROM houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Recycling Snakes', (SELECT id FROM houses WHERE name = 'Slytherin')),
    (gen_random_uuid(), 'Eco Serpents', (SELECT id FROM houses WHERE name = 'Slytherin')),

    -- Ravenclaw (Public Transport Teams)
    (gen_random_uuid(), 'Carbon Busters', (SELECT id FROM houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Green Commuters', (SELECT id FROM houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Broomstick Riders', (SELECT id FROM houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Eco Navigators', (SELECT id FROM houses WHERE name = 'Ravenclaw')),
    (gen_random_uuid(), 'Sustainable Voyagers', (SELECT id FROM houses WHERE name = 'Ravenclaw'));
