close all; clear;

Ns = [20000];
rs = [150, ];
% wcs = {'0.0', '0.0017', '0.0033', '0.0067', '0.0083', '0.013', '0.017'};
wcs = {'0.0', };
speeds = {'25.0',};
percentagesOfPriority = {'0.0', '0.2'};
for speed=speeds
    for wc=wcs
        wc = wc{1};
        for N=Ns
            for r=rs
                for percentageOfPriority=percentagesOfPriority
                    file_part = ['RTTA_', num2str(N), '_', num2str(r), '.0_', wc, '_', speed{1}, '_', percentageOfPriority{1}];
                    filename = ['./results/', file_part, '.json'];
    
                    data = jsondecode(fileread(filename));
    
                    for i=1:length(data.res)
                        start_times = [data.res(i).drones.desired_start];
                        [~, sortIdx] = sort(start_times);
                        data.res(i).drones = struct2table(data.res(i).drones(sortIdx));
                    end
    
                    save(['./results_converted/', file_part, '.mat'], "data", "-v7.3");
                end
            end
        end
    end
end