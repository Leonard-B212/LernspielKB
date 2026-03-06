document.addEventListener('DOMContentLoaded', () => {
    const items = document.querySelectorAll('.draggable-item');
    items.forEach(item => {
        item.addEventListener('dragstart', e => {
            e.dataTransfer.setData('text/plain', e.target.id);
            e.dataTransfer.effectAllowed = 'move';
            item.classList.add('dragging');
            item._originZone = item.parentElement; // remember origin
        });
        item.addEventListener('dragend', () => {
            item.classList.remove('dragging');
            if (item._originZone) compactGrid(item._originZone);
        });
    });

    const zones = document.querySelectorAll('.box-top, .box-bottom');
    zones.forEach(zone => {
        zone.addEventListener('dragover', e => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
        });

        zone.addEventListener('drop', e => {
            e.preventDefault();
            const id = e.dataTransfer.getData('text/plain');
            const dragged = document.getElementById(id);
            if (!dragged) return;

            const { row: tr, col: tc } = cellFromPoint(zone, e.clientX, e.clientY);
            const { row, col } = findAllowedCell(zone, tr, tc, dragged);

            if (dragged.parentElement !== zone) {
                zone.appendChild(dragged);
                dragged._originZone = zone;
            }
            dragged.style.gridRowStart    = row;
            dragged.style.gridColumnStart = col;

            compactGrid(zone);            // <<–– compact after the drop
        });
    });

    function findAllowedCell(container, targetR, targetC, dragged) {
        const occupied = new Set();
        container.querySelectorAll('.draggable-item').forEach(elem => {
            if (elem === dragged || elem.classList.contains('dragging')) return;
            const r = parseInt(elem.style.gridRowStart, 10) || 1;
            const c = parseInt(elem.style.gridColumnStart, 10) || 1;
            occupied.add(`${r},${c}`);
        });

        if (occupied.size === 0) {
            return { row: 2, col: 1 };
        }

        const candidates = new Set();
        occupied.forEach(key => {
            const [r, c] = key.split(',').map(Number);
            const right = `${r},${c+1}`;
            const below = `${r+1},${c}`;
            if (!occupied.has(right)) candidates.add(right);
            if (!occupied.has(below)) candidates.add(below);
        });

        let best = null;
        let bestDist = Infinity;
        const dist = (r,c) => {
            const dr = r - targetR;
            const dc = c - targetC;
            return dr*dr + dc*dc;
        };

        candidates.forEach(k => {
            const [r,c] = k.split(',').map(Number);
            if (best === null || dist(r,c) < bestDist) {
                best = { row: r, col: c };
                bestDist = dist(r,c);
            }
        });

        return best || { row: 2, col: 1 };
    }

    function cellFromPoint(container, x, y) {
        const rect = container.getBoundingClientRect();
        const cols = parseInt(container.dataset.cols, 10) || 4;
        const style = window.getComputedStyle(container);
        const gap = parseFloat(style.columnGap) || 0;

        const header = container.querySelector('.box-header');
        const offsetY = header
            ? y - (header.getBoundingClientRect().bottom + gap)
            : y - rect.top;

        const cellWidth = (rect.width - (cols - 1) * gap) / cols;
        let cellHeight = cellWidth;
        const first = container.querySelector('.draggable-item');
        if (first) {
            cellHeight =
                first.getBoundingClientRect().height +
                parseFloat(style.rowGap || 0);
        }

        let col = Math.floor((x - rect.left) / (cellWidth + gap));
        let row = Math.floor(offsetY / cellHeight) + 2;

        if (col < 0) col = 0;
        if (col >= cols) col = cols - 1;
        if (row < 2) row = 2;

        return { row, col: col + 1 };
    }

    /**
     * Slide every item as far left *and* as far up as possible without
     * changing the relative order.
     */
    function compactGrid(container) {
        const items = [...container.querySelectorAll('.draggable-item')];

        // compress each row independently
        items.forEach(el => {
            const r = parseInt(el.style.gridRowStart, 10) || 2;
            const rowItems = items
                .filter(o => (parseInt(o.style.gridRowStart, 10) || 2) === r)
                .sort((a, b) =>
                    (parseInt(a.style.gridColumnStart, 10) || 1) -
                    (parseInt(b.style.gridColumnStart, 10) || 1));
            rowItems.forEach((o, i) => {
                o.style.gridColumnStart = i + 1;
            });
        });
    }

});