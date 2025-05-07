clear; close all;

% xs = [];
% as = 0.1:0.01:2;
% % xs = arrayfun(@(a) integral(@(f) sqrt(1 + a^2 + 2 * a * cos(f)), 0, 2*pi), as);
% 
% % fun = @(f, a) sqrt(1+a^2+2*a*cos(f));
% 
% for a = as
% %     xs = [xs, integral(@(f) sqrt(1+a^2+2*a*cos(f)),0,2*pi)/(2*pi)];
% %     res = integral(@(f) sqrt(1 + 2*a*(cos(f) - abs(sin(f))) + a*a*(1-2*cos(f)*abs(sin(f))) ),0,2*pi);
%     res = integral(@(f) sqrt((1 + a.*cos(f) - a.*abs(sin(f))).^2),0,2*pi);
%     xs = [xs, res];
% end
% 
% plot(as, xs)
% xlabel('a')
% ylabel('integral')

% syms wind_speed;
% drone_speed = 25;
% desired_drone_speed = 20;
% 
% Sw = solve(integral(@(f) sqrt((drone_speed + wind_speed.*cos(f) - wind_speed.*abs(sin(f))).^2), 0, 2*pi) / (2*pi) == desired_drone_speed);
% 


% Define the parameters
% drone_speed = 25;
% desired_drone_speed = 20;
% 
% % Define the function to integrate
% integrand = @(f, wind_speed) sqrt((drone_speed + wind_speed .* cos(f) - wind_speed .* abs(sin(f))).^2);
% 
% % Define the function whose root we want to find
% objective = @(wind_speed) integral(@(f) integrand(f, wind_speed), 0, 2*pi) / (2*pi) - desired_drone_speed;
% 
% % Use fzero to find the wind_speed that makes the average speed equal to desired_drone_speed
% initial_guess = 0; % You can adjust this initial guess if needed
% wind_speed_solution = fzero(objective, initial_guess);
% 
% % Display the result
% disp(['The wind speed that results in the desired drone speed is: ', num2str(wind_speed_solution)]);

% drone_speed = 25; wind_speeds = [5, 10, 15, 20];
% 
% for wind_speed=wind_speeds
%     integral(@(f) (drone_speed + wind_speed.*cos(f) - wind_speed.*abs(sin(f))), 0, 2*pi) / (2*pi)
%     (drone_speed * 2 * pi - wind_speed*4)/(2*pi)
% end


% drone_speed = 25; wind_speeds = [5, 10, 15, 20];
% for wind_speed=wind_speeds
%     square_wds = (wind_speed.^2) / (drone_speed.^2);
%     
% end

drone_speed = 25;
wind_speeds = [5, 10, 15, 20];

for wind_speed=wind_speeds
    square_wds = (wind_speed.^2) / (drone_speed.^2);
    integral(@(f) (wind_speed.*cos(f) + drone_speed*sqrt(1-(square_wds .* sin(f) .* sin(f)))  ), 0, 2*pi) / (2*pi)
end

