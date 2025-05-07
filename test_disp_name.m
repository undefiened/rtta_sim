clear; close all;

wcs = {'0.0', '0.0017', '0.0033', '0.0067', '0.0083'};

for wc=wcs
    wc = wc{1};
    
    if str2num(wc) ~= 0
%         disp_name = 1/str2num(wc);
%         disp_name = round(disp_name/10)*10/60;
        wc_m_per_min = 1/(round(((1/str2num(wc))/60)*10)/10);
        disp_name = [sprintf('%.2f', wc_m_per_min), ' m/min'];
        disp_name = [num2str(wc_m_per_min), ' m/min'];
    else
        disp_name = 'No uncertainty';
    end
    
    disp(disp_name);
end