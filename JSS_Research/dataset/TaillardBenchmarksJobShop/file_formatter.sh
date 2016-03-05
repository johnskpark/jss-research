 
#!/bin/bash

output_dir="TaillardInsts"

mkdir "${output_dir}"

for file in tai*_*.txt; do
	prefix="${file/.txt/_}"
	jobs=`echo ${file} | sed 's/tai//g' | cut -d '_' -f 1`
	machines=`echo ${file} | sed 's/.txt//g' | cut -d '_' -f 2`

	echo "${jobs} ${machines}"

	csplit --quiet --prefix="${prefix}" "${file}" "/Times/" "{*}"
	rm "${prefix}00"

	for inst in "${prefix}"*; do
		sed -n '/Nb of jobs/q;p' ${inst} > temp 
		mv temp ${inst}

		touch temp
		echo "# number of machines" >> temp
		echo ${machines} >> temp
		echo "" >> temp

		echo "# number of routes" >> temp
		echo ${jobs} >> temp
		echo "" >> temp

		echo "# machine orders" >> temp
		sed -n '/Machines/,$p' ${inst} | sed -e '1d'  >> temp
		echo "" >> temp

		echo "# processing times for each operation on a route" >> temp
		sed -n '/Machines/q;p' ${inst} | sed -e '1d' >> temp
		echo "" >> temp

		echo "# job specifications, jobs have to be ordered by release date" >> temp
		echo "jobs" >> temp
		echo "" >> temp

		echo "# number of jobs" >> temp
		echo ${jobs} >> temp
		echo "" >> temp

		echo "# <route number> <release date> <due date> <weight>" >> temp
		i=1
		while [ "${i}" -le "${jobs}" ]; do
			echo "${i} 0 0 1" >> temp
			i=$(($i + 1))
		done
		echo "" >> temp

		mv temp ${inst}
		mv ${inst} ${inst}.txt
	done

	mv "${prefix}"* "${output_dir}"
done